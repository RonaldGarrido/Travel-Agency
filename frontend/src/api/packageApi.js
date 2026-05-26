import api from './axiosConfig'

export const getAvailablePackages = (filters = {}) => {
  return api.get('/packages/available', { params: filters })
}

export const getPackageById = (id) => {
  return api.get(`/packages/${id}`)
}

export const getAllPackages = () => {
  return api.get('/packages')
}

export const createPackage = (data) => {
  return api.post('/packages', data)
}

export const updatePackage = (id, data) => {
  return api.put(`/packages/${id}`, data)
}

export const changePackageStatus = (id, status) => {
  return api.patch(`/packages/${id}/status`, null, { params: { status } })
}

export const deletePackage = (id) => {
  return api.delete(`/packages/${id}`)
}