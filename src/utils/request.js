import axios from 'axios'
import useUserStore from '@/store/modules/userStore.js'



const request = axios.create({
    baseURL: 'http://localhost:8000',
    timeout: 5000,
    withCredentials: true,
})


export default request
