import { Deletable } from "@/types/Deletable";
import WithId from "@/types/WithId";
import User from "@/types/User";

type Post = PostBase & {
    user: User,
}

type PostWithoutUser = PostBase & {
    userId: number,
}

type PostBase = Deletable & WithId & {
    text: string,
    isArticle: boolean,
    likes: number,
    comments: number,
}

export type PostRequest = {
    text: string,
    isArticle: boolean,
    userId: number
}

export default Post;
