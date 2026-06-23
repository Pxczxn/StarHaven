import React from 'react';
import { ConfigProvider, theme } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import AppRouter from './router';
import './styles/global.css';
import 'dayjs/locale/zh-cn';
import dayjs from 'dayjs';

dayjs.locale('zh-cn');

function App() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        algorithm: theme.darkAlgorithm,
        token: {
          colorPrimary: '#8b5cf6',
          colorSuccess: '#06b6d4',
          colorWarning: '#fb923c',
          colorError: '#ec4899',
          colorInfo: '#3b82f6',
          colorBgContainer: 'rgba(11, 9, 28, 0.65)',
          colorBgElevated: '#110f24',
          colorBorder: 'rgba(255, 255, 255, 0.06)',
          colorText: '#f8fafc',
          colorTextSecondary: '#94a3b8',
          borderRadius: 12,
        },
      }}
    >
      <AppRouter />
    </ConfigProvider>
  );
}

export default App;
