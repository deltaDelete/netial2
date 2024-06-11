import { Message } from "@/types/Message";
import { useAuthContext } from "@/utils/AuthContext";
import { createResource } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import UserComponent from "@components/User";
import "./MessageComponent.css"
import { createQuery } from "@tanstack/solid-query";

export default function MessageComponent(props: {data: Message}) {
    const [user] = useAuthContext();

    const messageUser = createQuery(() => ({
        queryKey: ["user", props.data.userId],
        queryFn: async () => await ApiClient.instance.users.get(props.data.userId)
    }));

    return (
        <div class="message"
             classList={{
                 "outgoing": (props.data.userId == user()!.sub),
                 "incoming": (props.data.userId != user()!.sub)
             }}>
            <UserComponent class="!text-[1rem] !normal-case underline !p-1 !font-semibold" user={messageUser.data} id={props.data.userId} />
            <p>{props.data.text}</p>
        </div>
    )
}