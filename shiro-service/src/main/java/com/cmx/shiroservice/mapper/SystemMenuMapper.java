package com.cmx.shiroservice.mapper;



import com.cmx.shiroapi.model.SystemMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemMenuMapper {

    void add(SystemMenu menu);

    void modify(SystemMenu user);

    void delete(Integer id);

    SystemMenu getById(Integer id);

    List<SystemMenu> query(Map<String, Object> param);

    List<SystemMenu> queryByRole(Map<String, Object> param);

    Long countByFilter(Map<String, Object> param);

}
