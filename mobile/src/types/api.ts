export interface UserDTO {
  id: string;
  name: string;
  email: string;
  avatarUrl?: string;
}

export interface BalanceDTO {
  netBalance: number;  // Saldo Líquido
  oweAmount: number;   // O que deves
  owedAmount: number;  // O que te devem
}

export interface GroupDTO {
  id: string;
  name: string;
  description: string;
  inviteCode: string;
  memberCount: number;
  userBalance: number; // Saldo do utilizador específico neste grupo (positivo ou negativo)
}

export interface ExpenseDTO {
  id: string;
  description: string;
  amount: number;
  paidById: string;
  paidByName: string;
  date: string;       // ISO string
  splitType?: string; // e.g. EQUAL, PERCENTAGE
}

export interface LoginResponse {
  token: string;
  user: UserDTO;
}
