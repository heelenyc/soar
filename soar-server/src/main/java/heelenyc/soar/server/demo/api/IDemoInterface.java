package heelenyc.soar.server.demo.api;

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
}
