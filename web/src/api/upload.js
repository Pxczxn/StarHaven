import request from './request';

export const uploadImage = (file) => {
  const formData = new FormData();
  formData.append('file', file);

  return request.post('/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const deleteImage = (url) => {
  return request.delete('/upload/image', {
    params: { url },
  });
};
