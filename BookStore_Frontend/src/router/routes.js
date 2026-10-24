/**
 * 路由表。頁面都用動態載入，使用到才下載對應的程式碼。
 * meta.requiresAuth: 需要登入才能進入（判斷請看 router/index.js）
 */
const AdminLayout = () => import('@/views/admin/AdminLayout.vue');
const AdminHomePage = () => import('@/views/admin/HomePage.vue');
const BookManage = () => import('@/views/admin/BookManage.vue');
const DeletedBookManage = () => import('@/views/admin/DeletedBookManage.vue');
const UserManage = () => import('@/views/admin/UserManage.vue');

const HomeLayout = () => import('@/views/user/home/HomeLayout.vue');
const HomeView = () => import('@/views/user/home/HomeView.vue');
const BookDetail = () => import('@/views/user/book/BookDetail.vue');
const SearchLayout = () => import('@/views/user/search/SearchLayout.vue');

const CartLayout = () => import('@/views/user/cart/CartLayout.vue');
const CartLargeScreen = () => import('@/views/user/cart/CartLargeScreen.vue');
const CheckoutLayout = () => import('@/views/user/cart/CheckoutLayout.vue');

const LoginLayout = () => import('@/views/user/login/LoginLayout.vue');
const LoginView = () => import('@/views/user/login/LoginView.vue');

const RegisterLayout = () => import('@/views/user/register/RegisterLayout.vue');
const RegisterView = () => import('@/views/user/register/RegisterView.vue');
const SentEmailInfo = () => import('@/views/user/register/SentEmailInfo.vue');
const SuccessInfo = () => import('@/views/user/register/SuccessInfo.vue');

const ResetPasswordLayout = () => import('@/views/user/resetPassword/ResetPasswordLayout.vue');
const ForgotPassword = () => import('@/views/user/resetPassword/ForgotPassword.vue');
const VerifyToken = () => import('@/views/user/resetPassword/VerifyToken.vue');
const ResetPassword = () => import('@/views/user/resetPassword/ResetPassword.vue');
const ResetPasswordSuccess = () => import('@/views/user/resetPassword/ResetPasswordSuccess.vue');

const MemberCenterLayout = () => import('@/views/user/memberCenter/MemberCenterLayout.vue');
const PersonalInfo = () => import('@/views/user/memberCenter/PersonalInfo.vue');
const BorrowRecord = () => import('@/views/user/memberCenter/BorrowRecord.vue');
const ChangePassword = () => import('@/views/user/memberCenter/ChangePassword.vue');
const VerifyPassword = () => import('@/views/user/verification/VerifyPassword.vue');

const ErrorPage = () => import('@/components/ErrorPage.vue');

export default [
    {
        path: '/admin',
        component: AdminLayout,
        meta: {requiresAuth: true},
        children: [
            {path: '', name: 'adminHomePage', component: AdminHomePage},
            {path: 'book', name: 'adminBookPage', component: BookManage},
            {path: 'deletedBook', name: 'adminDeletedBookPage', component: DeletedBookManage},
            {path: 'user', name: 'adminUserPage', component: UserManage}
        ]
    },
    {
        path: '/',
        component: HomeLayout,
        children: [
            {path: '', name: 'home', component: HomeView},
            {path: 'product/detail/:slug/:isbn', name: 'bookDetail', component: BookDetail},
            {path: 'search', name: 'search', component: SearchLayout}
        ]
    },
    {
        path: '/cart',
        component: CartLayout,
        children: [
            {path: '', name: 'cart', component: CartLargeScreen}
        ]
    },
    {
        path: '/checkout',
        name: 'checkout',
        component: CheckoutLayout,
        meta: {requiresAuth: true}
    },
    {
        path: '/login',
        component: LoginLayout,
        children: [
            {path: '', name: 'login', component: LoginView}
        ]
    },
    {
        path: '/register',
        component: RegisterLayout,
        children: [
            {path: '', name: 'register', component: RegisterView},
            {path: 'successInfo', name: 'successInfo', component: SuccessInfo}
        ]
    },
    {
        path: '/sent-email-info',
        name: 'sentEmailInfo',
        component: SentEmailInfo
    },
    {
        path: '/forgot-password',
        component: ResetPasswordLayout,
        children: [
            {path: '', name: 'forgotPassword', component: ForgotPassword},
            {path: 'verify-token', name: 'verifyToken', component: VerifyToken},
            {path: 'reset-password', name: 'resetPassword', component: ResetPassword},
            {path: 'reset-password-success', name: 'resetPasswordSuccess', component: ResetPasswordSuccess}
        ]
    },
    {
        path: '/member-center',
        component: MemberCenterLayout,
        name: 'memberCenter',
        redirect: '/member-center/personal-info',
        meta: {requiresAuth: true},
        children: [
            {path: 'personal-info', name: 'personalInfo', component: PersonalInfo},
            {path: 'borrowRecord', name: 'borrowRecord', component: BorrowRecord}
        ]
    },
    {
        path: '/change-password',
        name: 'changePassword',
        component: ChangePassword,
        meta: {requiresAuth: true}
    },
    {
        path: '/verify-password',
        name: 'verifyPassword',
        component: VerifyPassword,
        meta: {requiresAuth: true}
    },
    {
        path: '/error',
        name: 'error',
        component: ErrorPage
    },
    {
        path: '*',
        redirect: '/'
    }
];
