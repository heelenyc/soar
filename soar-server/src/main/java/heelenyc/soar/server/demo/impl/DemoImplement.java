package heelenyc.soar.server.demo.impl;


import heelenyc.soar.core.demo.api.IDemoInterface;
import heelenyc.soar.core.demo.api.ParamsBean;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        try {
             TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            // TODO: handle exception
        }
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
        HashSet<Double> ret = new HashSet<Double>();
        ret.add(2.0d);
        ret.add(1.0d);
        return ret;
    }

    @Override
    public Set<ParamsBean> getBeanSet() {
        ParamsBean bean = new ParamsBean();
        bean.setOp1(2.1d);
        bean.setOp2(3.1d);
        
        HashSet<ParamsBean> ret = new HashSet<ParamsBean>();
        ret.add(bean);
        return ret;
    }

    @Override
    public Map<String, Double> getDoubleMap() {
        HashMap<String,Double> ret = new HashMap<String,Double>();
        ret.put("lisi",2.0d);
        ret.put("zhangsan",1.0d);
        return ret;
    }

    @Override
    public Map<String, ParamsBean> getBeanMap() {
        ParamsBean bean = new ParamsBean();
        bean.setOp1(2.1d);
        bean.setOp2(3.1d);
        
        HashMap<String,ParamsBean> ret = new HashMap<String,ParamsBean>();
        ret.put("zhangsan",bean);
        return ret;
    }

    @Override
    public Double[] getDoublePArray() {
        return (Double[]) Arrays.asList(1.0d,2.1d).toArray();
    }

    @Override
    public ParamsBean[] getBeanPArray() {
        ParamsBean bean = new ParamsBean();
        bean.setOp1(2.1d);
        bean.setOp2(3.1d);
        return (ParamsBean[]) Arrays.asList(bean,bean).toArray();
    }

    @Override
    public Double addListDouble(List<Double> beanList) {
        return 3.0d;
    }

}
