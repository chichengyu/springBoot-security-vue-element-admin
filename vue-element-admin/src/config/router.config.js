/************************ axios请求 ************************/
import { baseUrl } from '@/common/env.js';

const config = {
	// baseUrl:'http://192.168.1.10:8000/admin'
	baseUrl:baseUrl,
	headers:{},
    withCredentials:true // 跨域配置
};
export default config;