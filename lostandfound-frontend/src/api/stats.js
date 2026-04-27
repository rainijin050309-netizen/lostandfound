import request from '../utils/request'

export const fetchCategoryStats = () => request.get('/api/stats/category')
export const fetchLocationStats = () => request.get('/api/stats/location')
export const fetchMonthlyStats = () => request.get('/api/stats/monthly')
export const fetchOverviewStats = () => request.get('/api/stats/overview')
