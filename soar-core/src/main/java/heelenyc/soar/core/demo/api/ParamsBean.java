package heelenyc.soar.core.demo.api;

import java.io.Serializable;

/**
 * @author yicheng
 * @since 2016年4月27日
 *
 */
public class ParamsBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7804076655174507525L;
    
    private Double op1;
    private Double op2;
    public Double getOp1() {
        return op1;
    }
    public void setOp1(Double op1) {
        this.op1 = op1;
    }
    public Double getOp2() {
        return op2;
    }
    public void setOp2(Double op2) {
        this.op2 = op2;
    }
    
}
