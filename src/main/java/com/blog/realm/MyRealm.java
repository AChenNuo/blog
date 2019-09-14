package com.blog.realm;

import com.blog.entity.Blogger;
import com.blog.service.BloggerService;
import com.blog.util.Const;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;


/**
 * 登录的时候再来实现
 */
public class MyRealm extends AuthorizingRealm {

    @Resource
    private BloggerService bloggerService;
    /**
     * 获取授权信息的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     *  登录验证
     *  token：令牌，基于用户名和密码的令牌
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //从令牌中取出用户名
        String userName = (String)authenticationToken.getPrincipal();
        //让shiro去验证账号密码
        Blogger blogger = bloggerService.getByUserName(userName);
        if (blogger != null){
            SecurityUtils.getSubject().getSession().setAttribute(Const.CURRENT_USER,blogger);
            //数据库查询出来的数据存在authenticationInfo这里面，而前台传来的数据存在token里面，最终token和authenticationInfo比较是否一致，不一致则错误
            AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(blogger.getUserName(), blogger.getPassword(), getName());
            return authenticationInfo;
        }

        return null;
    }
}
