import React, { useEffect, useState } from 'react';
import { Button, Card, Form, Input, message } from 'antd';
import { SaveOutlined } from '@ant-design/icons';
import { getSiteSetting, updateSiteSetting } from '../../api/settings';
import './index.css';

const { TextArea } = Input;

const Settings = () => {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [preview, setPreview] = useState({});
  const [form] = Form.useForm();

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await getSiteSetting();
      form.setFieldsValue(response.data);
      setPreview(response.data || {});
    } catch (error) {
      message.error(error.message || '加载设置失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);
      const response = await updateSiteSetting(values);
      setPreview(response.data || values);
      form.setFieldsValue(response.data || values);
      message.success('站点设置已保存');
    } catch (error) {
      if (error.errorFields) return;
      message.error(error.message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="settings-page">
      <h2 className="page-title">系统设置</h2>
      <Card className="settings-shell" loading={loading}>
        <div className="setting-preview">
          <div className="setting-brand">{preview.brandName || '星栖民宿'}</div>
          <div className="setting-slogan">{preview.slogan || '星辰为引，栖心而居'}</div>
          <div className="setting-meta">{preview.description || '在星空下，找到心灵的栖息地'}</div>
          <div className="setting-meta">电话：{preview.phone || '-'}</div>
          <div className="setting-meta">地址：{preview.address || '-'}</div>
        </div>

        <Form
          form={form}
          layout="vertical"
          onValuesChange={(_, values) => setPreview(values)}
        >
          <Form.Item label="品牌名称" name="brandName" rules={[{ required: true, message: '请输入品牌名称' }]}>
            <Input placeholder="星栖民宿" />
          </Form.Item>
          <Form.Item label="首页标语" name="slogan" rules={[{ required: true, message: '请输入首页标语' }]}>
            <Input placeholder="星辰为引，栖心而居" />
          </Form.Item>
          <Form.Item label="联系电话" name="phone" rules={[{ required: true, message: '请输入联系电话' }]}>
            <Input placeholder="400-888-8888" />
          </Form.Item>
          <Form.Item label="地址" name="address" rules={[{ required: true, message: '请输入地址' }]}>
            <Input placeholder="请输入民宿地址" />
          </Form.Item>
          <Form.Item label="站点简介" name="description">
            <TextArea rows={4} placeholder="用于 H5 首页和联系页面展示" />
          </Form.Item>
          <Form.Item label="首页主图地址" name="heroImageUrl">
            <Input placeholder="可选，填写图片 URL" />
          </Form.Item>
          <Button type="primary" icon={<SaveOutlined />} loading={saving} onClick={handleSubmit}>
            保存设置
          </Button>
        </Form>
      </Card>
    </div>
  );
};

export default Settings;
