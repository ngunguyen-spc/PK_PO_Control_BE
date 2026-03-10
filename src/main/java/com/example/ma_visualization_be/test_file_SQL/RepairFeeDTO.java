package com.example.ma_visualization_be.test_file_SQL;

public class RepairFeeDTO {
    private String dept;
    private Double act;
    private Double tgtMtdOrg;

    public RepairFeeDTO(String dept, Double act, Double tgtMtdOrg) {
        this.dept = dept;
        this.act = act;
        this.tgtMtdOrg = tgtMtdOrg;
    }

    public String getDept() {
        return dept;
    }

    public Double getAct() {
        return act;
    }

    public Double getTgtMtdOrg() {
        return tgtMtdOrg;
    }
}

