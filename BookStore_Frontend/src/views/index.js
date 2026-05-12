export const AdminMain = () => import("@/views/admin/Main.vue");
export const AdminHomePage = () => import("@/views/admin/HomePage.vue");
export const BookManage = () => import("@/views/admin/BookManage.vue");
export const UserManage = () => import("@/views/admin/UserManage.vue");

export const LoginMain = () => import("@/views/user/login/Main.vue");
export const Login = () => import("@/views/user/login/Login.vue");

export const RegisterMain = () => import("@/views/user/register/RegisterLayout.vue");
export const Register = () => import("@/views/user/register/Register.vue");
export const SentEmailInfo = () => import("@/views/user/register/SentEmailInfo.vue");
export const SuccessInfo = () => import("@/views/user/register/SuccessInfo.vue");

export const ForgetPassword = () => import("@/views/user/resetPassword/ForgotPassword.vue");
export const VerifyToken = () => import("@/views/user/resetPassword/VerifyToken.vue");
export const ResetPassword = () => import("@/views/user/resetPassword/ResetPassword.vue");
export const ResetPasswordSuccess = () => import("@/views/user/resetPassword/ResetPasswordSuccess.vue");
export const ResetPasswordMain = () => import("@/views/user/resetPassword/ResetPasswordLayout.vue");

export const MemberCenterMain = () => import("@/views/user/memberCenter/MemberCenterLayout.vue");
export const PersonalInfo = () => import("@/views/user/memberCenter/PersonalInfo.vue");
export const BorrowRecord = () => import("@/views/user/memberCenter/BorrowRecord.vue");

export const ErrorPage = () => import("@/components/errorPage.vue");

export const UserMain = () => import("@/views/user/home/HomeLayout.vue");
export const UserHomePage = () => import("@/views/user/home/Home.vue");

export const DeletedBookManage = () => import("@/views/admin/DeletedBookManage.vue");
export const ChangePassword = () => import("@/views/user/memberCenter/ChangePassword.vue");
export const VerifyPassword = () => import("@/views/user/verification/VerifyPassword.vue");
export const SearchBooks = () => import("@/views/user/search/SearchLayout.vue");
export const BookDetail = () => import("@/views/user/book/BookDetail.vue");
export const Cart = () => import("@/views/user/cart/Cart_Lg_Screen.vue");
export const CartMain = () => import("@/views/user/cart/CartLayout.vue");

export const CheckoutMain = ()=>import("@/views/user/cart/Checkout.vue")
export const Checkout = ()=>import("@/views/user/cart/CheckoutLayout.vue")
