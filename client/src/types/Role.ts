import Deletable from "@/types/Deletable";
import WithId from "@/types/WithId";
import { Permission } from "@/types/Permission";

type Role = Deletable & WithId & RoleBase

export type RoleBase = {
    name: string,
    permissions: Permission,
    description: string
}

export default Role;

