import React, { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, message } from 'antd';
import {
  DashboardOutlined,
  HomeOutlined,
  AppstoreOutlined,
  ShoppingOutlined,
  UserOutlined,
  DollarOutlined,
  ToolOutlined,
  TeamOutlined,
  SettingOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import { logout as logoutApi } from '../api/auth';

const { Header, Sider, Content } = Layout;

const AdminLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, clearAuth } = useAuthStore();
  const [collapsed, setCollapsed] = useState(false);

  // 菜单项配置
  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '数据看板',
    },
    {
      key: '/room-types',
      icon: <AppstoreOutlined />,
      label: '房型管理',
    },
    {
      key: '/rooms',
      icon: <HomeOutlined />,
      label: '房间管理',
    },
    {
      key: '/orders',
      icon: <ShoppingOutlined />,
      label: '订单管理',
    },
    {
      key: '/customers',
      icon: <UserOutlined />,
      label: '客户管理',
    },
    {
      key: '/finance',
      icon: <DollarOutlined />,
      label: '财务统计',
    },
    {
      key: '/operations',
      icon: <ToolOutlined />,
      label: '运营任务',
    },
    {
      key: '/users',
      icon: <TeamOutlined />,
      label: '账号管理',
      visible: user?.role === 'admin', // 只有管理员可见
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '系统设置',
      visible: user?.role === 'admin', // 只有管理员可见
    },
  ]
    .filter(item => item.visible !== false)
    .map(({ visible, ...item }) => item);

  // 处理菜单点击
  const handleMenuClick = ({ key }) => {
    navigate(key);
  };

  // 处理退出登录
  const handleLogout = async () => {
    try {
      await logoutApi();
      clearAuth();
      message.success('退出登录成功');
      navigate('/login');
    } catch (error) {
      message.error(error.message || '退出登录失败');
      // 即使退出失败也清除本地状态
      clearAuth();
      navigate('/login');
    }
  };

  // 用户下拉菜单
  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人信息',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  return (
    <div className="admin-shell">
      {/* 星空背景层 */}
      <div className="admin-shell__bg"></div>
      <div className="star-layer-1"></div>
      <div className="star-layer-2"></div>
      <div className="star-layer-3"></div>

      <Layout className="admin-layout" style={{ background: 'transparent', position: 'relative', zIndex: 1 }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        width={272}
        className="admin-sider"
      >
        <div className="logo">
          <div className="logo-icon">
            {collapsed ? '星' : '星'}
          </div>
          {!collapsed && <span className="logo-text">星栖</span>}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header className="admin-header">
          <div style={{ flex: 1 }}></div>
          <div className="header-right">
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <div className="user-info">
                <Avatar icon={<UserOutlined />} />
                <span className="user-name">{user?.realName || user?.username}</span>
              </div>
            </Dropdown>
          </div>
        </Header>
        <Content className="admin-content">
          <div className="content-wrapper">
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
    </div>
  );
};

export default AdminLayout;
