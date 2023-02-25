import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

    // previous added up
    private BigDecimal preResult;

    // new added up
    private BigDecimal newNum;

    // store the operand from the beginning
    private List<BigDecimal> historyNumList = new ArrayList<>();

    // store the operation from the beginning
    private List<String> historyOptList = new ArrayList<>();

    // store the result from the beginning
    private List<BigDecimal> historyResultList = new ArrayList<>();

    // current operator
    private String curOperator;

    // index of last used operator
    private int lastOptIndex = -1;

    // default scale
    private int scale = 2;

    // record the max index of valid operator, for undo/redo
    private int validIndexMax = -1;

    public BigDecimal getPreResult() {
        return preResult;
    }

    public void setPreResult(BigDecimal preResult) {
        this.preResult = preResult;
    }

    public BigDecimal getNewNum() {
        return newNum;
    }

    public void setNewNum(BigDecimal newNum) {
        if(preResult == null){
            preResult = newNum;
        }else{
            this.newNum = newNum;
        }
    }

    public List<BigDecimal> getHistoryNumList() {
        return historyNumList;
    }

    public void setHistoryNumList(List<BigDecimal> historyNumList) {
        this.historyNumList = historyNumList;
    }

    public List<String> getHistoryOptList() {
        return historyOptList;
    }

    public void setHistoryOptList(List<String> historyOptList) {
        this.historyOptList = historyOptList;
    }

    public String getCurOperator() {
        return curOperator;
    }

    public void setCurOperator(String curOperator) {
        this.curOperator = curOperator;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public List<BigDecimal> getHistoryResultList() {
        return historyResultList;
    }

    public void setHistoryResultList(List<BigDecimal> historyResultList) {
        this.historyResultList = historyResultList;
    }

    public int getLastOptIndex() {
        return lastOptIndex;
    }

    public void setLastOptIndex(int lastOptIndex) {
        this.lastOptIndex = lastOptIndex;
    }

    public int getValidIndexMax() {
        return validIndexMax;
    }

    public void setValidIndexMax(int validIndexMax) {
        this.validIndexMax = validIndexMax;
    }

    /**
     *  do calculation
     */
    public void calc(){
        preResult = preResult == null ? BigDecimal.ZERO : preResult;
        if(curOperator == null){
            System.out.println("please select operation!");
        }
        if(newNum != null){
            BigDecimal ret = calcTwoNum(preResult, curOperator, newNum);
            if(this.lastOptIndex == -1){ // not in redo/undo process
                historyResultList.add(preResult);
                historyNumList.add(newNum);
                historyOptList.add(curOperator);
            }else{ // within redo/undo process, store into history list and record in validIndexMax
                this.lastOptIndex++;
                this.validIndexMax = this.lastOptIndex;
                this.historyResultList.set(this.lastOptIndex, ret);
                this.historyNumList.set(this.lastOptIndex-1, newNum);
                this.historyOptList.set(this.lastOptIndex-1, curOperator);
            }
            preResult = ret;
            curOperator = null;
            newNum = null;
        }
    }

    /**
     * undo
     */
    public void undo(){
        if(preResult != null && lastOptIndex == -1){ // not in redo/undo process store in history
            historyResultList.add(preResult);
            curOperator = null;
            newNum = null;
        }

        if(historyResultList.size() == 0){
            System.out.println("No operation!");
        }else if(historyResultList.size() == 1){
            System.out.println("after undo: 0,"+"before undo: "+ preResult);
            preResult = BigDecimal.ZERO;
        } else {
            if(lastOptIndex == -1){
                lastOptIndex = historyOptList.size()-1;
            }else{
                if(lastOptIndex-1 < 0){
                    System.out.println("can not undo!");
                    return;
                }
                lastOptIndex--;
            }
            cancelPreOperate(historyResultList.get(lastOptIndex), historyOptList.get(lastOptIndex), historyNumList.get(lastOptIndex));
        }
    }

    /**
     *  redo
     */
    public void redo(){
        try{
            if(lastOptIndex > -1){
                if(lastOptIndex + 1 == historyResultList.size() || lastOptIndex+1 == this.validIndexMax+1){
                    // check whether the there is next operator in the history list or the next operator has been overwritten by a new operator
                    System.out.println("can not redo!");
                    return;
                }
                lastOptIndex++;

                redoOperate(historyResultList.get(lastOptIndex), historyOptList.get(lastOptIndex-1), historyNumList.get(lastOptIndex-1));
            }
        }catch (Exception e){
            System.out.println("redo exception, lastOptIndex:"+lastOptIndex);
        }
    }

    private void redoOperate(BigDecimal redoTotal, String redoOpt, BigDecimal redoNum) {
        System.out.println("after redo: "+redoTotal+", before redo:"+ preResult +", operator of redo:"+redoOpt+", operand of redo: "+redoNum);
        preResult = redoTotal;
        curOperator = null;
        newNum = null;
    }

    private void cancelPreOperate(BigDecimal lastTotal, String lastOpt, BigDecimal lastNum) {
        System.out.println("after undo: "+lastTotal+", before undo:"+ preResult +", operator of undo: "+lastOpt+", operand of undo: "+lastNum);
        preResult = lastTotal;
        curOperator = null;
        newNum = null;
    }

    /**
     * calculation
     * @param preTotal previous total
     * @param curOperator current operator
     * @param newNum new input number
     * @return
     */
    private BigDecimal calcTwoNum(BigDecimal preTotal, String curOperator, BigDecimal newNum) {
        BigDecimal ret = BigDecimal.ZERO;
        curOperator = curOperator == null ? "+" : curOperator;
        switch (curOperator){
            case "+":
                ret = preTotal.add(newNum);
                break;
            case "-":
                ret = preTotal.subtract(newNum).setScale(scale, RoundingMode.HALF_UP);
                break;
            case "*":
                ret = preTotal.multiply(newNum).setScale(scale, RoundingMode.HALF_UP);
                break;
            case "/":
                ret = preTotal.divide(newNum, RoundingMode.HALF_UP);
                break;
        }
        return ret;
    }

    /**
     * result demonstration
     */
    public String display(){
        StringBuilder sb = new StringBuilder();
        if(preResult != null){
            sb.append(preResult.setScale(scale, RoundingMode.HALF_DOWN));
        }
        if(curOperator != null){
            sb.append(curOperator);
        }
        if(newNum != null){
            sb.append(newNum);
        }
        System.out.println(sb);
        return sb.toString();
    }
}