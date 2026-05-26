import api from './axiosConfig'

export const getSalesReport = (startDate, endDate) => {
  return api.get('/reports/sales', { params: { startDate, endDate } })
}

export const getPackageRanking = (startDate, endDate) => {
  return api.get('/reports/packages/ranking', { params: { startDate, endDate } })
}