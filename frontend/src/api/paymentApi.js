import api from './axiosConfig'

export const getPaymentById = (id) => {
  return api.get(`/payments/${id}`)
}

export const getPaymentByReservation = (reservationId) => {
  return api.get(`/payments/reservation/${reservationId}`)
}

export const processPayment = (data) => {
  return api.post('/payments', data)
}