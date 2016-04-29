package heelenyc.soar.core.demo.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yicheng
 * @since 2016年4月27日
 * 
 */
public interface IDemoInterface {
    
    String echo(String msg);
    
    int addInt(int op1, int op2);
    
    Double addDouble(Double op1, Double op2);
    
    double add(ParamsBean bean);
    
    List<Double> getDoubleList();
    
    List<ParamsBean> getBeanList();
    
    Set<Double> getDoubleSet();
    
    Set<ParamsBean> getBeanSet();
    
    
    Map<String,Double> getDoubleMap();
    
    Map<String,ParamsBean> getBeanMap();
    
    Double[] getDoublePArray();
    
    ParamsBean[] getBeanPArray();

    Double addList(List<ParamsBean> beanList);
}
