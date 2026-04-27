import request from '../utils/request'

export const createClaim = (data) => request.post('/api/claim', data)
export const getClaimsByItemId = (itemId) => request.get(`/api/claim/item/${itemId}`)
export const getClaimsByUserId = (userId) => request.get(`/api/claim/user/${userId}`)
export const updateClaimStatus = (id, status) => request.put(`/api/claim/${id}/status`, null, { params: { status } })
