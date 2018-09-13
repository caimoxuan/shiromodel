package com.cmx.shiroapi.commons;

import com.cmx.shiroapi.enums.ErrorMessageEnum;
import lombok.Data;
import java.util.List;

@Data
public class ResultData<T> {

    private List<T> items;
    private int code = 200;
    private boolean success;
    private String message;
    private Object data;
    private int totalCount;

    public static ResultData newFail(Integer code, String message){
        ResultData resultSet = new ResultData();
        resultSet.setCode(code);
        resultSet.setSuccess(false);
        resultSet.setMessage(message);
        return resultSet;
    }

    public static ResultData newSetSuccess(List items, int totalCount){
        ResultData resultSet = new ResultData();
        resultSet.setSuccess(true);
        resultSet.setItems(items);
        resultSet.setTotalCount(totalCount);
        return resultSet;
    }

    public static ResultData newSingleSuccess(Object data){
        ResultData resultSet = new ResultData();
        resultSet.setData(data);
        resultSet.setSuccess(true);
        return resultSet;
    }

    public static ResultData newFail(ErrorMessageEnum eme){
        ResultData resultSet = new ResultData();
        resultSet.setCode(eme.getCode());
        resultSet.setMessage(eme.getMessage());
        resultSet.setSuccess(false);
        return resultSet;
    }


}
