package com.example.ma_visualization_be.repository;

import com.example.ma_visualization_be.dto.IRemainTableDTO;
import com.example.ma_visualization_be.dto.IRepairFeeDTO;
import com.example.ma_visualization_be.model.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRemainTableRepo extends JpaRepository<DummyEntity, Long> {
    @Query(value = """
            DECLARE @date DATE = :date;
                                DECLARE @div NVARCHAR(10) = :div;
            
                                WITH exl AS
                                (
                                    SELECT *
                                    FROM (
                                        SELECT *,
                                               ROW_NUMBER() OVER (PARTITION BY VBELN ORDER BY ID DESC) rn
                                        FROM F2_PackingList
                                        WHERE SSD = @date
                                        AND [check] = 'Fix'
                                    ) t
                                    WHERE rn = 1
                                ),
            
                                wo AS
                                (
                                    SELECT *
                                    FROM (
                                        SELECT wo1.*,
                                               ROW_NUMBER() OVER (PARTITION BY MPO ORDER BY ID DESC, JKBN DESC) rn
                                        FROM MANUFASPCPD.dbo.MANUFA_F_PD_W_ORDER wo1
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
            
                                check_fn AS
                                (
                                    SELECT\s
                                        CONVERT(date, CAST(exl_sum.EDATU AS nvarchar(10)),112) AS SSD,
                                        exl_sum.VBELN,
                                        exl_sum.Qty,
                                        pk_sum.sum_Qty,
                                        exl_sum.ShipBy,
                                        exl_sum.CusID,
            
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
            
                                    INNER JOIN MANUFASPCPD.dbo.MANUFA_F_PD_GRB_PRODUCT pd
                                        ON exl_sum.PHTX = pd.MAKTX
            
                                    LEFT JOIN pk_sum
                                        ON exl_sum.VBELN = pk_sum.VBELN
                                )
            
                                SELECT\s
                                    cus.CusGrp,
                                    check_fn.CusID,
                                    check_fn.ShipBy,
                                    COUNT(check_fn.VBELN) AS Ex_PO,
                                    SUM(check_fn.qty) AS Ex_Qty,
            
                                    COUNT(CASE WHEN check_fn.fn_status = 'OK' THEN check_fn.VBELN END) AS Fn_PO,
                                    SUM(check_fn.sum_Qty) AS Fn_Qty,
            
                                    COUNT(CASE WHEN check_fn.fn_status = 'NY' THEN check_fn.VBELN END) AS Remain_PO,
                                    CASE\s
                                    WHEN SUM(check_fn.qty) - SUM(check_fn.sum_Qty) < 0\s
                                    THEN 0
                                    ELSE SUM(check_fn.qty) - SUM(check_fn.sum_Qty)
                                	END AS Remain_Qty
                                FROM check_fn
            
                                LEFT JOIN F2_Cus_Grp cus ON check_fn.CusID = cus.CusID
            
                                WHERE
                                (
                                    @div = 'KVH'
                                    OR (@div = 'PRESS' AND check_fn.div LIKE 'PR%')
                                    OR (@div = 'GUIDE' AND check_fn.div LIKE '%G')
                                    OR (@div = 'MOLD'  AND check_fn.div LIKE 'MO')
                                )
            
                                GROUP BY cus.CusGrp, check_fn.CusID, check_fn.ShipBy
            
                                ORDER BY
                                    CASE CusGrp
                                        WHEN 'SRG' THEN 1
                                        WHEN 'DL'  THEN 2
                                        WHEN 'MSM' THEN 3
                                        ELSE 4
                                    END,
                                    Remain_Qty DESC;
            	""", nativeQuery = true)
    List<IRemainTableDTO> getRemainTable(@Param("div") String div, @Param("date") String date);


}
