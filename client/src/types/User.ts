import Deletable from "@/types/Deletable";
import WithId from "@/types/WithId";

type User = Deletable & WithId & {
    lastName: string,
    firstName: string,
    birthDate: number,
    userName: string,
    email: string,
    isEmailConfirmed: boolean,
    lastLoginDate: number
}

export default User;