export const getApiErrorMessage = (error, fallbackMessage = 'Something went wrong.') => {
  const data = error?.response?.data;

  if (!data) {
    return fallbackMessage;
  }

  if (typeof data === 'string') {
    return data;
  }

  if (data.message) {
    return data.message;
  }

  if (data.error) {
    return data.error;
  }

  if (typeof data === 'object') {
    const fieldErrors = Object.values(data).filter(Boolean);
    if (fieldErrors.length > 0) {
      return fieldErrors.join(', ');
    }
  }

  return fallbackMessage;
};
