import request from './request';

/**
 * 获取站点信息
 */
export const getSiteInfo = () => {
  return request.get('/public/site-info');
};
