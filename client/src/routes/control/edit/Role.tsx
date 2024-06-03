import { A, useLocation, useNavigate, useParams } from "@solidjs/router";
import Post from "@/types/Post";
import role from "@/types/Role";
import { createMemo, createResource, For, Show } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import Input from "@components/InputComponent";
import { Button } from "@kobalte/core/button";
import { Select } from "@kobalte/core/select";
import { Permission } from "@/types/Permission";
import { Checkbox } from "@kobalte/core/checkbox";
import { Loading } from "@components/Loading";
import CheckboxComponent from "@components/CheckboxComponent";

export default function Role() {
    const params = useParams<{ id: string }>();
    const id = () => Number(params.id);
    const navigate = useNavigate();
    const location = useLocation<{ role: role }>();

    const [role, { mutate }] = createResource(id, async (k: number) => {
        return await ApiClient.instance.roles.get(k);
    }, {
        ssrLoadFrom: "initial",
        initialValue: location.state?.role
    });

    const handlePermissionChange = (checked: boolean, permission: Permission) => {
        mutate((prev) => {
            if (!prev) return prev;
            if (!checked) {
                // Удаляем
                prev.permissions = prev.permissions & ~permission;
                console.log("New permission:", prev);
                return prev;
            }
            // Добавляем
            prev.permissions = prev.permissions | permission;
            console.log("New permission:", prev);
            return prev;
        });
    };

    const onSubmit = (e: Event) => {
        e.preventDefault();
        role() && ApiClient.instance.roles.update(role()!).then(value => {
            navigate("/control?tab=roles");
        });
    };

    return (
        <div class="flex flex-col gap-4 self-stretch items-stretch">
            <form onSubmit={onSubmit} class="flex flex-col gap-4 container root-container">
                <Show when={role.state == "ready"} fallback={<Loading />}>

                    <Input name="name" type="text" multiline={false} onChange={value => mutate((prev) => {
                        if (prev)
                            prev.name = value;
                        return prev;
                    })} label="Название" value={role()?.name} />
                    <Input name="description" type="text" multiline={false} onChange={value => mutate((prev) => {
                        if (prev)
                            prev.description = value;
                        return prev;
                    })} label="Описание" value={role()?.description} />

                    <ul class="flex max-sm:flex-col flex-row gap-1 flex-wrap">
                        <For
                            each={Object.values(Permission).filter(i => Number.isInteger(Number(i))).map(i => Number(i))}>{(item) =>
                            <li class="flex flex-row gap-2 basis-1/3 grow">
                                {/*<input type="checkbox" id={`permission-${Permission[item]}`}*/}
                                {/*       checked={(role()!.permissions & item) == item}*/}
                                {/*       onChange={() => handlePermissionChange(item)} />*/}
                                {/*<span>{Permission[item]}</span>*/}
                                <CheckboxComponent defaultChecked={(role()!.permissions & item) == item}
                                                   label={Permission[item]}
                                                   onChange={(checked) => handlePermissionChange(checked, item)}
                                                   id={`permission-${Permission[item]}`} />
                            </li>
                        }</For>
                    </ul>

                    <div class="flex max-sm:flex-col flex-row gap-2">
                        <Button class="button primary grow basis-1/2" type="submit">Сохранить</Button>
                        <A class="button secondary grow basis-1/2" href={"/control?tab=roles"}>Назад</A>
                    </div>
                </Show>
            </form>
        </div>
    );
}