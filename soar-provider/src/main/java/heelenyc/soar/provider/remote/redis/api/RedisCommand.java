package heelenyc.soar.provider.remote.redis.api;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yicheng
 * @since 2016年1月11日
 * 
 */
public class RedisCommand {

    /** Command name */
    private final String action;

    /** Optional arguments */
    private List<byte[]>  argList = new ArrayList<byte[]>();
    
    private String sourceHostport = "";
    
    public RedisCommand(String name) {
        this.action = name;
    }
    
    public RedisCommand(String name,String sourceHostport) {
        this.action = name;
        this.sourceHostport = sourceHostport;
    }

    public List<byte[]> getArgList() {
        return argList;
    }

    public void setArgList(List<byte[]> argList) {
        this.argList = argList;
    }

    public String getAction() {
        return action;
    }
    
    /**
     * get command param based 0
     * @param index
     * @return
     */
    public String getArg(int index){
        try {
            return new String(argList.get(index));
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not enough params");
        }
    }
    
    /**
     * return the remaining args, include the arg at beginIndex,index based 0
     * @param beginIndex
     * @return
     */
    public String[] getRemainArgs(int beginIndex){
        try {
            List<byte[]> sublist = argList.subList(beginIndex, argList.size());
            String[] remains = new String[sublist.size()];
            for (int i = 0; i < sublist.size(); i++) {
                remains[i] = new String(sublist.get(i));
            }
            return remains;
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not enough params");
        }
    }

    @Override
    public String toString() {
        ArrayList<String> argListStr = new ArrayList<String>();
        for (byte[] string : argList) {
            argListStr.add(new String(string));
        }
        return "RedisCommand [sourceHostport] [action=" + action + ", argList=" + argListStr + "]";
    }

    public String getSourceHostport() {
        return sourceHostport;
    }

    public void setSourceHostport(String sourceHostport) {
        this.sourceHostport = sourceHostport;
    }
}
