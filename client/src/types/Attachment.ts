import WithId from "@/types/WithId";
import Deletable from "@/types/Deletable";
import User from "@/types/User";

export type AttachmentBase = {
    name: string,
    mimeType: string,
    hash: string,
    size: number,
    userId: number,
    user?: User
}

export type Attachment = WithId & Deletable & AttachmentBase