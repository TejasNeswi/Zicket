const extensionByContentType = {
  'application/pdf': 'pdf',
  'image/jpeg': 'jpg',
  'image/png': 'png',
  'image/webp': 'webp',
  'text/plain': 'txt',
};

const getFilenameFromHeader = (contentDisposition) => {
  if (!contentDisposition) {
    return null;
  }

  const match = contentDisposition.match(/filename="?([^"]+)"?/i);
  return match?.[1] || null;
};

export const downloadResponseBlob = (response, fallbackName) => {
  const contentType = response.headers?.['content-type'] || 'application/octet-stream';
  const filename =
    getFilenameFromHeader(response.headers?.['content-disposition']) ||
    `${fallbackName}.${extensionByContentType[contentType] || 'bin'}`;

  const blob = new Blob([response.data], { type: contentType });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.parentNode.removeChild(link);
  window.URL.revokeObjectURL(url);
};
