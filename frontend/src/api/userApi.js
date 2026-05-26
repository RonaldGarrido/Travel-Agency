import api from './axiosConfig'

export const getAllUsers = () => {
  return api.get('/users')
}

export const getUserById = (id) => {
  return api.get(`/users/${id}`)
}

export const createUser = (data) => {
  return api.post('/users', data)
}

export const updateUser = (id, data) => {
  return api.put(`/users/${id}`, data)
}

export const changeUserStatus = (id, status) => {
  return api.patch(`/users/${id}/status`, null, { params: { status } })
}