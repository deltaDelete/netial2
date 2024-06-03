import { Navigate, useLocation, useNavigate } from "@solidjs/router";
import { createResource, Show } from "solid-js";
import ApiClient from "@/utils/ApiClient";

export default function Confirm() {
    const location = useLocation<{ userId: number } | undefined>();
    const userId = () => location.state?.userId;

    const [user] = createResource(userId, (id) => ApiClient.instance.users.get(id));

    return (
        <>
            <Show when={!userId()}>
                <Navigate href={"/"} />
            </Show>
            <div class="container root-container">
                <h1 class="text-2xl text-center heading uppercase">Подтверждение</h1>
                <Show when={user()}>
                    <p>Письмо с подтверждением было отправлено на вашу электронную почту.</p>
                    <p>{user()!.email}</p>
                </Show>
            </div>
        </>
    );
}