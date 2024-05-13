import Deletable from "@/types/Deletable";
import WithId from "@/types/WithId";
import User from "@/types/User";

export type Comment = Deletable & WithId & {
    text: string,
    postId: string,
    user: User
}

export type CommentRequest = {
    text: String,
    postId: number,
    userId: number
}

export default Comment;