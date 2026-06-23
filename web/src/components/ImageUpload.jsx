import React, { useState } from 'react';
import { Upload, message } from 'antd';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons';
import { uploadImage } from '../api/upload';

const ImageUpload = ({ value, onChange, maxCount = 1 }) => {
  const [loading, setLoading] = useState(false);
  const [imageUrl, setImageUrl] = useState(value || '');

  const beforeUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
      message.error('只能上传图片文件！');
      return false;
    }
    const isLt5M = file.size / 1024 / 1024 < 5;
    if (!isLt5M) {
      message.error('图片大小不能超过 5MB！');
      return false;
    }
    return true;
  };

  const handleChange = async (info) => {
    if (info.file.status === 'uploading') {
      setLoading(true);
      return;
    }
    if (info.file.status === 'done') {
      const url = info.file.response?.data?.url;
      if (url) {
        setImageUrl(url);
        setLoading(false);
        onChange?.(url);
        message.success('上传成功');
      }
    }
    if (info.file.status === 'error') {
      setLoading(false);
      message.error('上传失败');
    }
  };

  const customRequest = async ({ file, onSuccess, onError }) => {
    try {
      const response = await uploadImage(file);
      onSuccess(response);
    } catch (error) {
      onError(error);
    }
  };

  const uploadButton = (
    <div>
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>上传图片</div>
    </div>
  );

  return (
    <Upload
      name="file"
      listType="picture-card"
      className="image-uploader"
      showUploadList={false}
      beforeUpload={beforeUpload}
      onChange={handleChange}
      customRequest={customRequest}
    >
      {imageUrl ? (
        <img src={imageUrl} alt="avatar" style={{ width: '100%' }} />
      ) : (
        uploadButton
      )}
    </Upload>
  );
};

export default ImageUpload;
