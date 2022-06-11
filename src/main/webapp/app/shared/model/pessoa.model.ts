import dayjs from 'dayjs';

export interface IPessoa {
  id?: number;
  name?: string;
  cpf?: string;
  email?: string;
  avatarContentType?: string | null;
  avatar?: string | null;
  birthDate?: string | null;
  excluded?: boolean | null;
}

export const defaultValue: Readonly<IPessoa> = {
  excluded: false,
};
