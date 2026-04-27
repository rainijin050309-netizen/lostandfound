import request from '../utils/request'

export const login = (data) => request.post('/api/user/login', data)
export const register = (data) => request.post('/api/user/register', data)
export const getUserById = (id) => request.get(`/api/user/${id}`)
