package com.cmx.shiroservice.comparator;

import com.cmx.shiroapi.model.SystemRole;

import java.util.Comparator;

public class SystemRoleComparator implements Comparator<SystemRole> {

    @Override
    public int compare(SystemRole role1, SystemRole role2) {
        return role1.getRoleCode().compareTo(role2.getRoleCode());
    }
}
