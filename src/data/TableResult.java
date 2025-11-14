package data;

public class TableResult {
    private Float[] probTable;
    private Integer[] aliasTable;

    public TableResult(Float[] probTable, Integer[] aliasTable) {
        this.probTable = probTable;
        this.aliasTable = aliasTable;
    }

    public Float[] getProbTable() {
        return probTable;
    }

    public Integer[] getAliasTable() {
        return aliasTable;
    }
}
