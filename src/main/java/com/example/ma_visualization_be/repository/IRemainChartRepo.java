package com.example.ma_visualization_be.repository;

import com.example.ma_visualization_be.dto.IRemainChartDTO;
import com.example.ma_visualization_be.model.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRemainChartRepo extends JpaRepository<DummyEntity, Long> {
    @Query(value = """
            DECLARE @date NVARCHAR(10) = :date;
                                DECLARE @div NVARCHAR(10) = :div;
            
                                WITh\s
                                	exl AS (SELECT *,
                                		ROW_NUMBER() OVER (PARTITION BY VBELN ORDER BY ID Desc) as rn
                                		FROM F2_PackingList
                                		WHERE [check] = 'Fix'
                                		AND SSD >= GETDATE()-7),
                                	exl_plan AS (
                                		SELECT dtl.VBELN, dtl.ZGLOBAL_CODE, dtl.EDATU, dtl.PHTX,
                                			COALESCE(dtl.KWmENG,exl.Qty) as Qty
                                		FROM MANUFASPCPD.dbo.MANUFA_F_PD_DT_ORDER_DTL dtl\s
                                		LEFT JOIN exl ON dtl.VBELN = exl.VBELN AND exl.rn = 1
                                		WHERE EDATU >= FORMAT(GETDATE()-7,'yyyyMMdd')),
                                	pk_sum AS (
                                		SELECT hed.KDAUF as VBELN, SUM(rec.TQty) as sum_Qty
                                		FROM QC_KAOshakaData_BK07 rec\s
                                		INNER JOIN MANUFASPCPD.dbo.MANUFA_F_PD_DT_REQ_HED hed ON rec.OrderNo = hed.AUFNR
                                		INNER JOIN exl_plan ON hed.KDAUF = exl_plan.VBELN
                                		GROUP BY hed.KDAUF),
                                	check_fn AS (
                                		SELECT CONVERT(DATE,CAST(exl_plan.EDATU as nvarchar(10)),112) as SSD, exl_plan.VBELN, exl_plan.Qty, pk_sum.sum_Qty,
                                			IIF(pk_sum.sum_Qty>=exl_plan.Qty, 'OK','NY') as fn_status,
                                			IIF((pd.FERTH LIKE '%Retainer%' or pd.FERTH LIKE '%Backing Plate%') AND pd.MAKTX Not Like '%BACKING PLUG%', 'PR-RET',
                                				LEFT(IIF(pd.PRODH like 'FA%48%' or pd.PRODH = 'MO   17' ,'PR',
                                				IIF(pd.PRODH like 'FA%99%','MO',pd.PRODH)),2)) As Div
                                		FROM exl_plan
                                		INNER JOIN MANUFASPCPD.dbo.MANUFA_F_PD_GRB_PRODUCT pd ON exl_plan.PHTX = pd.MAKTX
                                		LEFT JOIN pk_sum ON exl_plan.VBELN = pk_sum.VBELN)
            
                                SELECT SSD, COUNT(fn_status) as ex_po,
                                	SUM(CASE WHEN fn_status = 'OK' THEN 1 ELSE 0 END) as fn_po,
                                	SUM(CASE WHEN fn_status = 'NY' THEN 1 ELSE 0 END) as remain_po
                                FROM check_fn
                                WHERE SSD BETWEEN DATEADD(DAY,-7,CAST(:date AS DATE))
                                              AND DATEADD(DAY,7,CAST(:date AS DATE))
                                AND (
                                	@div = 'KVH'
                                	OR (:div = 'PRESS' AND check_fn.div LIKE 'PR%')
                                	OR (:div = 'GUIDE' AND check_fn.div LIKE '%G')
                                	OR (:div = 'MOLD' AND check_fn.div LIKE 'MO')
                                	)
                                GROUP BY SSD
                                ORDER BY SSD
            
            	""", nativeQuery = true)
    List<IRemainChartDTO> getRemainChart(@Param("div") String div, @Param("date") String date);


}
