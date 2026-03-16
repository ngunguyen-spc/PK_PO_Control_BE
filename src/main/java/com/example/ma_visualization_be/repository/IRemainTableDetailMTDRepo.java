package com.example.ma_visualization_be.repository;

import com.example.ma_visualization_be.dto.IRemainTableDetailDTO;
import com.example.ma_visualization_be.dto.IRemainTableDetailMTDDTO;
import com.example.ma_visualization_be.model.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRemainTableDetailMTDRepo extends JpaRepository<DummyEntity, Long> {
    @Query(value = """
            DECLARE @date DATE = :date;
            DECLARE @div NVARCHAR(10) = :div;
            DECLARE @cusID NVARCHAR(20) = :cusID;
            DECLARE @shipBy NVARCHAR(10) = :shipBy;
            
            WITH exl AS
            (
                SELECT *
                FROM (
                    SELECT *,
                           ROW_NUMBER() OVER (PARTITION BY VBELN ORDER BY ID DESC) rn
                    FROM F2_PackingList
                    --WHERE SSD = @date
            		WHERE SSD >= DATEADD(DAY, -7, @date)
                    AND [check] = 'Fix'
                ) t
                WHERE rn = 1
            ),
            
            wo AS
            (
                SELECT *
                FROM (
                    SELECT wo1.*,
                           ROW_NUMBER() OVER (PARTITION BY MPO ORDER BY wo1.ID DESC, wo1.JKBN DESC) rn
                    FROM MANUFASPCPD.dbo.MANUFA_F_PD_W_ORDER wo1
            		INNER JOIN exl ON wo1.MPO = exl.PO
                ) t
                WHERE rn = 1
            ),
            
            exl_sum AS
            (
                SELECT\s
                    exl.VBELN,
                    exl.PO,
                    exl.qty,
                    exl.SSD,
                    dtl.EDATU,
                    dtl.KWMENG,
                    dtl.RRONYU1 AS CusID,
                    dtl.ABGRU,
                    dtl.PHTX,
            		dtl.RONAME,
            		IIF(dtl.RODENK LIKE '1%', 'MTO', 'MTS') AS DENK,
                    CASE wo.TRANSPORT
                        WHEN 'OCEAN' THEN 'SEA'
                        WHEN 'Express' THEN 'EXP'
                        ELSE wo.TRANSPORT
                    END AS ShipBy
                FROM exl
                INNER JOIN MANUFASPCPD.dbo.MANUFA_F_PD_DT_ORDER_DTL dtl ON exl.VBELN = dtl.VBELN
                INNER JOIN wo ON exl.PO = wo.MPO
            	WHERE (ABGRU is null or ABGRU = '60')
            ),
            
            pk_sum AS (
                SELECT hed.KDAUF AS VBELN,
                    SUM(rec.TQty) AS sum_Qty
                FROM QC_KAOshakaData_BK07 rec
                INNER JOIN MANUFASPCPD.dbo.MANUFA_F_PD_DT_REQ_HED hed ON rec.OrderNo = hed.AUFNR
                INNER JOIN exl ON hed.KDAUF = exl.VBELN
                GROUP BY hed.KDAUF
            	),
            
            pickup AS (
            	SELECT *,
            		COALESCE(Day_Adjust,Day_STD) as Day1, COALESCE(Hour_Adjust,Hour_STD) as Hour1	
            		FROM F2Database.dbo.F2_Pickup_Time
            	),
            
            check_fn AS
            (
                SELECT\s
                    CONVERT(date, CAST(exl_sum.EDATU AS nvarchar(10)),112) AS SSD1,
                    pk_sum.sum_Qty,
                    exl_sum.*,
            		pd.FERTH,
            
                    CASE\s
                        WHEN pk_sum.sum_Qty >= exl_sum.Qty THEN 'OK'
                        ELSE 'NY'
                    END AS fn_status,
            
                    CASE
                        WHEN (pd.FERTH LIKE '%Retainer%'\s
                           OR pd.FERTH LIKE '%Backing Plate%')
                         AND pd.MAKTX NOT LIKE '%BACKING PLUG%'
                        THEN 'PR-RET'
            
                        ELSE LEFT(
                                CASE
                                    WHEN pd.PRODH LIKE 'FA%48%' OR pd.PRODH = 'MO   17'
                                        THEN 'PR'
                                    WHEN pd.PRODH LIKE 'FA%99%'
                                        THEN 'MO'
                                    ELSE pd.PRODH
                                END
                            ,2)
                    END AS Div
            
                FROM exl_sum
                INNER JOIN MANUFASPCPD.dbo.MANUFA_F_PD_GRB_PRODUCT pd ON exl_sum.PHTX = pd.MAKTX
                LEFT JOIN pk_sum ON exl_sum.VBELN = pk_sum.VBELN
            )
            
            SELECT\s
            	check_fn.SSD1 as SSD,
            	FORMAT(
                  DATEADD(MINUTE, Hour1*60, DATEADD(DAY, Day1,CAST(SSD AS DATETIME))),
                  'yyyy-MM-dd HH:mm'
                ) as PickupTime,
                check_fn.CusID,
                check_fn.ShipBy,
            	check_fn.DENK,
            	check_fn.VBELN,
            	check_fn.PO,	
            	check_fn.Div,
            	check_fn.FERTH,
            	check_fn.RONAME,
                check_fn.qty AS Ex_Qty,
                check_fn.sum_Qty AS Fn_Qty,
                CASE\s
                WHEN check_fn.qty - COALESCE(check_fn.sum_Qty,0) < 0 THEN 0
                ELSE check_fn.qty - COALESCE(check_fn.sum_Qty,0)
            	END AS Remain_Qty
            FROM check_fn
            LEFT JOIN pickup ON check_fn.CusID = pickup.CusID AND check_fn.ShipBy = pickup.ShipBy
            WHERE
            (
                @div = 'KVH'
                OR (@div = 'PRESS' AND check_fn.div LIKE 'PR%')
                OR (@div = 'GUIDE' AND check_fn.div LIKE '%G')
                OR (@div = 'MOLD'  AND check_fn.div LIKE 'MO')
            )
            AND (@cusID = 'All' OR check_fn.CusID = @cusID)
            AND (@shipBy = 'All' OR check_fn.ShipBy = @shipBy)
            AND DATEADD(MINUTE, Hour1*60, DATEADD(DAY, Day1,CAST(SSD AS DATETIME))) >= @date
            
            ORDER BY Remain_Qty DESC, CusID, ShipBy
            	""", nativeQuery = true)
    List<IRemainTableDetailMTDDTO> getRemainTableDetailMTD(@Param("div") String div,
                                                           @Param("date") String date,
                                                           @Param("cusID") String cusID,
                                                           @Param("shipBy") String shipBy);

}
