import request from '../utils/request'

export const runLlmQuery = (query) => request.post('/api/llm-query', { query })
