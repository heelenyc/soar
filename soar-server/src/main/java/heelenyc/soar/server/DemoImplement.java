package heelenyc.soar.server;

import heelenyc.soar.server.demo.api.IDemoInterface;
import heelenyc.soar.server.demo.api.ParamsBean;

/**
 * @author yicheng
 * @since 2016年4月27日
 *
 */
public class DemoImplement implements IDemoInterface {

    @Override
    public String echo(String msg) {
        return msg;
    }

    @Override
    public int addInt(int op1, int op2) {
        return op1 + op2;
    }
    
    @Override
    public Double addDouble(Double op1, Double op2) {
        return op1 + op2;
    }

    @Override
    public double add(ParamsBean bean) {
        return bean.getOp1() + bean.getOp2();
    }

}
