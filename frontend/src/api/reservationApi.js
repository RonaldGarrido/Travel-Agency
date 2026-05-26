import api from './axiosConfig'

export const getAllReservations = () => {
  return api.get('/reservations')
}

export const getReservationById = (id) => {
  return api.get(`/reservations/${id}`)
}

export const getReservationsByUser = (userId) => {
  return api.get(`/reservations/user/${userId}`)
}

export const createReservation = (data) => {
  return api.post('/reservations', data)
}

export const cancelReservation = (id) => {
  return api.patch(`/reservations/${id}/cancel`)
}