export interface ApiBase<T> {
    delete(id: number): Promise<Response>;

    totalPages(): Promise<number>;

    get(id: number): Promise<T>;

    getAll(page?: number): Promise<T[]>;

    update(newItem: T): Promise<T>;
}