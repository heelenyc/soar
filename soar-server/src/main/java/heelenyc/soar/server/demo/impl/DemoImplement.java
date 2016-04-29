package heelenyc.soar.server.demo.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import heelenyc.soar.core.demo.api.IDemoInterface;
import heelenyc.soar.core.demo.api.ParamsBean;

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
    
    @Override
    public Double addList(List<ParamsBean> beanList) {
        return beanList.get(0).getOp1() + beanList.get(1).getOp2();
    }

    @Override
    public List<Double> getDoubleList() {
        return Arrays.asList(1.2d,2.0d);
    }

    @Override
    public List<ParamsBean> getBeanList() {
        ParamsBean bean = new ParamsBean();
        bean.setOp1(2.1d);
        bean.setOp2(3.1d);
        
        return Arrays.asList(bean,bean);
    }

    @Override
    public Set<Double> getDoubleSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ParamsBean> getBeanSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Double> getDoubleMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, ParamsBean> getBeanMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double[] getDoublePArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ParamsBean[] getBeanPArray() {
        // TODO Auto-generated method stub
        return null;
    }

}
