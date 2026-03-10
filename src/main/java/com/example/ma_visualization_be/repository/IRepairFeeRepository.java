package com.example.ma_visualization_be.repository;

import com.example.ma_visualization_be.dto.IRepairFeeDTO;
import com.example.ma_visualization_be.model.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRepairFeeRepository extends JpaRepository<DummyEntity, Long> {
    @Query(value = """
          DECLARE @month VARCHAR(6) = REPLACE(:month, '-', '');
          WITH MA_Dept as (
            SELECT IIF(ERFMG<0,-1,1)*AMOUNT as ACT,
                CASE
                    WHEN XBLNR2 LIKE '1566-%' THEN\s
                        CASE
                            WHEN LEFT(KOSTL,6) IN ('614100','614000') THEN 'PRESS'
                            WHEN LEFT(KOSTL,6) = '614200' THEN 'MOLD'
                            WHEN LEFT(KOSTL,6) IN ('614600', '614700') THEN 'GUIDE'
                        END
                    ELSE 'OTHER'
                END as Dept,
                Left(BLDAT,4) + '-' + SUBSTRING(BLDAT,5,2) as mth,
                KOSTL,
                XBLNR2
            FROM MANUFASPCPD.dbo.MANUFA_F_PD_DTM_ISSUE
            WHERE KONTO = '570600'
            AND LEFT(BLDAT,6) = @month
            AND BLDAT < CONVERT(varchar,GETDATE(),112))

          SELECT COALESCE(ratio.DIV,MA_Dept.Dept) as Dept, fc.FC_USD, dc.CountDayAll,dc.CountDayMTD,(fc.FC_USD * dc.CountDayMTD / dc.CountDayAll) as TGT_MTD_ORG, SUM(Act*COALESCE(ratio.Ratio,1)) as ACT
          FROM MA_Dept
          
          LEFT JOIN F2Database.dbo.F2_Cost_Center_Share share ON MA_Dept.KOSTL = share.Cost_Center
          LEFT JOIN F2Database.dbo.F2_Cost_Center_Share_Ratio ratio ON share.Group_Share = ratio.Dept
                                                                    AND CASE
                                                                        WHEN XBLNR2 LIKE '1566-%' THEN\s
                                                                            CASE
                                                                                WHEN LEFT(KOSTL,6) IN ('614100','614000') THEN 'PRESS'				
                                                                                WHEN LEFT(KOSTL,6) = '614200' THEN 'MOLD'				
                                                                                WHEN LEFT(KOSTL,6) IN ('614600', '614700') THEN 'GUIDE'	
                                                                            END
                                                                        ELSE 'OTHER'
                                                                        END <> 'OTHER'\s
          LEFT JOIN F2Database.dbo.F2_CostFC_Tool_ByDept fc  ON 'MA_' + COALESCE(ratio.DIV,MA_Dept.Dept) = fc.Dept AND MA_Dept.mth = fc.Year_Month
          LEFT JOIN\s
                (
                    SELECT FORMAT([Date],'yyyy-MM') as mth,SUM(WD_Office) as CountDayAll,\s
                    SUM(IIF([Date]<CAST(GETDATE() as date),1,0)*WD_Office) as CountDayMTD
                    FROM F2Database.dbo.F2_Working_Date
                    WHERE FORMAT([Date],'yyyyMM') = @month
                    GROUP BY FORMAT([Date],'yyyy-MM')
                ) as dc
                ON fc.Year_Month = dc.mth
          GROUP BY COALESCE(ratio.DIV,MA_Dept.Dept),fc.FC_USD, dc.CountDayAll,dc.CountDayMTD
            """, nativeQuery = true)
    List<IRepairFeeDTO> getRepairFee(@Param("month") String month);

}
