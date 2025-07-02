import {get, post} from "@/net";
import {useStore} from "@/store";
import {ElMessage} from "element-plus";

export const apiUserInfo = (loadingRef) => {
    loadingRef.value = true
    const store = useStore()
    get('api/user/info', (data)=>{
        store.user = data
        loadingRef.value = false
    })
}

export const apiUserRegister = (data, success) => {
    post('/api/auth/register', data, success)
}

export const apiUserValidateEmail = (email, coldTime, type = 'register') => {
    get(`/api/auth/ask-code?email=${email}&type=${type}`, () => {
        coldTime.value = 60
        ElMessage.success(`验证码已发送到邮箱: ${email}，请注意查收`)
        const handle = setInterval(() => {
            coldTime.value--
            if(coldTime.value === 0) {
                clearInterval(handle)
            }
        }, 1000)
    }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
    })
}

export const apiUserResetConfirm = (data, activeRef) => {
    post('/api/auth/reset-confirm', data, () => activeRef.value++)
}

export const apiUserResetPassword = (data,success) => {
    post('/api/auth/reset-password', data, success)
}

export const apiUserChangePassword = (form, success) => {
    post('api/user/change-password', form, success)
}

export const apiUserPrivacy = (success) => {
    get('/api/user/privacy', success)
}

export const apiUserPrivacySave = (data, savingRef) => {
    savingRef.value = true
    post('api/user/save-privacy', data, () =>{
        ElMessage.success('隐私设置修改成功！')
        savingRef.value = false
    }, )
}

export const apiUserDetailsSave = (data, success, failure) => {
    post('/api/user/save-details', data, success, failure)
}

export const apiUserDetails = (success) => {
    get('/api/user/details', success)
}

export const apiUserEmailModify = (form, success) => {
    post('api/user/modify-email', form, success)
}

export const apiNotificationList = (success) =>
    get('api/notification/list', success)

export const apiNotificationDelete = (id,success) =>
    get(`api/notification/delete?id=${id}`, success)

export const apiNotificationDeleteAll = (success) =>
    get('api/notification/delete-all', success)

export const apiAdminUserList = (page, size,success) =>
    get(`api/admin/list?page=${page}&size=${size}`, success)

export const apiAdminUserDetails = (id, success) =>
    get(`api/admin/details?id=${id}`,success)

export const apiAdminUserDetailsSave = (data,success) =>
    post('api/admin/save', data, success)