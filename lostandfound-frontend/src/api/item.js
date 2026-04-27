import request from '../utils/request'

export const createItem = (data) => request.post('/api/item', data)
export const getItemById = (id) => request.get(`/api/item/${id}`)
export const queryItems = (params) => request.get('/api/item/list', { params })
export const updateItemStatus = (id, status) => request.put(`/api/item/${id}/status`, null, { params: { status } })
export const deleteItem = (id) => request.delete(`/api/item/${id}`)
