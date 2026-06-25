import { UserDTO, BalanceDTO, GroupDTO, ExpenseDTO, LoginResponse } from '../types/api';

// Configuração base de URL para quando ligar ao backend em Java
export const API_BASE_URL = 'http://localhost:8080/api'; // Ajustar para o IP/URL do servidor Spring Boot

// Simulação de atraso de rede (delay) para os mocks
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Dados Mockados de Teste
let mockUser: UserDTO = {
  id: 'usr-001',
  name: 'Alex Silva',
  email: 'alex.silva@example.com',
  avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&h=150&q=80'
};

let mockBalance: BalanceDTO = {
  netBalance: 114.50,
  oweAmount: 25.50,
  owedAmount: 140.00
};

let mockGroups: GroupDTO[] = [
  {
    id: 'grp-1',
    name: 'Viagem a Paris 🗼',
    description: 'Despesas da viagem de férias em grupo',
    inviteCode: 'PARIS2026',
    memberCount: 4,
    userBalance: -25.50 // O utilizador deve 25.50
  },
  {
    id: 'grp-2',
    name: 'Casa - Renda & Contas 🏠',
    description: 'Despesas partilhadas do apartamento T2',
    inviteCode: 'CASAT2XP',
    memberCount: 3,
    userBalance: 140.00 // Devem 140.00 ao utilizador
  },
  {
    id: 'grp-3',
    name: 'Jantar de Aniversário 🎂',
    description: 'Prenda e jantar do aniversário da Rita',
    inviteCode: 'RITAANIV',
    memberCount: 12,
    userBalance: 0.00 // Saldos acertados
  }
];

let mockExpenses: Record<string, ExpenseDTO[]> = {
  'grp-1': [
    {
      id: 'exp-101',
      description: 'Bilhetes do Museu do Louvre',
      amount: 60.00,
      paidById: 'usr-002',
      paidByName: 'Ana Martins',
      date: '2026-06-08T14:30:00Z'
    },
    {
      id: 'exp-102',
      description: 'Jantar no Bistrô de Paris',
      amount: 120.00,
      paidById: 'usr-001', // Pago pelo user atual
      paidByName: 'Tu',
      date: '2026-06-07T21:00:00Z'
    },
    {
      id: 'exp-103',
      description: 'Uber Aeroporto -> Hotel',
      amount: 36.50,
      paidById: 'usr-003',
      paidByName: 'Carlos Santos',
      date: '2026-06-06T11:15:00Z'
    }
  ],
  'grp-2': [
    {
      id: 'exp-201',
      description: 'Supermercado Mensal',
      amount: 280.00,
      paidById: 'usr-001',
      paidByName: 'Tu',
      date: '2026-06-05T18:45:00Z'
    },
    {
      id: 'exp-202',
      description: 'Conta da Eletricidade',
      amount: 90.00,
      paidById: 'usr-004',
      paidByName: 'Maria Clara',
      date: '2026-06-01T10:00:00Z'
    }
  ],
  'grp-3': []
};

// --- SERVIÇOS DA API ---

export const AuthService = {
  login: async (email: string, password: string): Promise<LoginResponse> => {
    await delay(800);
    // Aqui seria feito: return axios.post(`${API_BASE_URL}/auth/login`, { email, password });
    if (email && password.length >= 6) {
      return {
        token: 'jwt-token-placeholder-xyz',
        user: { ...mockUser, email }
      };
    }
    throw new Error('Credenciais inválidas. Palavra-passe deve ter pelo menos 6 caracteres.');
  },

  register: async (name: string, email: string, password: string): Promise<LoginResponse> => {
    await delay(800);
    // Aqui seria feito: return axios.post(`${API_BASE_URL}/auth/register`, { name, email, password });
    return {
      token: 'jwt-token-placeholder-new',
      user: {
        id: 'usr-' + Math.random().toString(36).substr(2, 9),
        name,
        email,
        avatarUrl: undefined
      }
    };
  },

  logout: async (): Promise<void> => {
    await delay(300);
  }
};

export const DashboardService = {
  getBalanceSummary: async (): Promise<BalanceDTO> => {
    await delay(400);
    // return axios.get(`${API_BASE_URL}/balances/summary`);
    return mockBalance;
  },

  getGroups: async (): Promise<GroupDTO[]> => {
    await delay(500);
    // return axios.get(`${API_BASE_URL}/groups`);
    return mockGroups;
  },

  createGroup: async (name: string, description: string): Promise<GroupDTO> => {
    await delay(600);
    // return axios.post(`${API_BASE_URL}/groups`, { name, description });
    const newGroup: GroupDTO = {
      id: 'grp-' + (mockGroups.length + 1),
      name,
      description,
      inviteCode: Math.random().toString(36).substr(2, 6).toUpperCase(),
      memberCount: 1,
      userBalance: 0.00
    };
    mockGroups.unshift(newGroup); // Adiciona no início da lista
    mockExpenses[newGroup.id] = [];
    return newGroup;
  },

  joinGroup: async (inviteCode: string): Promise<GroupDTO> => {
    await delay(600);
    // return axios.post(`${API_BASE_URL}/groups/join`, { inviteCode });
    const code = inviteCode.toUpperCase();
    const foundGroup = mockGroups.find(g => g.inviteCode === code);
    
    if (foundGroup) {
      return foundGroup;
    }
    
    // Se não estiver na lista local, criar um mock baseado no código
    const newGroup: GroupDTO = {
      id: 'grp-joined-' + Math.random().toString(36).substr(2, 5),
      name: `Grupo Convidado (${code})`,
      description: 'Grupo aderido via código de convite',
      inviteCode: code,
      memberCount: 5,
      userBalance: 0.00
    };
    mockGroups.push(newGroup);
    mockExpenses[newGroup.id] = [];
    return newGroup;
  }
};

export const GroupService = {
  getGroupDetails: async (groupId: string): Promise<GroupDTO> => {
    await delay(300);
    // return axios.get(`${API_BASE_URL}/groups/${groupId}`);
    const group = mockGroups.find(g => g.id === groupId);
    if (!group) throw new Error('Grupo não encontrado.');
    return group;
  },

  getGroupExpenses: async (groupId: string): Promise<ExpenseDTO[]> => {
    await delay(400);
    // return axios.get(`${API_BASE_URL}/groups/${groupId}/expenses`);
    return mockExpenses[groupId] || [];
  },

  addExpense: async (groupId: string, data: { description: string; amount: number; paidById: string; paidByName: string }): Promise<ExpenseDTO> => {
    await delay(600);
    // return axios.post(`${API_BASE_URL}/groups/${groupId}/expenses`, data);
    
    const newExpense: ExpenseDTO = {
      id: 'exp-' + Math.random().toString(36).substr(2, 9),
      description: data.description,
      amount: data.amount,
      paidById: data.paidById,
      paidByName: data.paidByName,
      date: new Date().toISOString()
    };

    if (!mockExpenses[groupId]) {
      mockExpenses[groupId] = [];
    }
    mockExpenses[groupId].unshift(newExpense);

    // Ajustar o balanço do utilizador no grupo
    const group = mockGroups.find(g => g.id === groupId);
    if (group) {
      if (data.paidById === 'usr-001') {
        // Se eu paguei, os outros devem-me (saldo do grupo sobe)
        // Mock rápido: adiciona uma porção do valor ao saldo do utilizador
        const portion = data.amount * ((group.memberCount - 1) / group.memberCount);
        group.userBalance += portion;
        mockBalance.netBalance += portion;
        mockBalance.owedAmount += portion;
      } else {
        // Se outro pagou, eu devo-lhe (saldo do grupo desce)
        const portion = data.amount / group.memberCount;
        group.userBalance -= portion;
        mockBalance.netBalance -= portion;
        mockBalance.oweAmount += portion;
      }
    }

    return newExpense;
  },

  addMemberToGroup: async (groupId: string, email: string): Promise<{ success: boolean; message: string }> => {
    await delay(500);
    // return axios.post(`${API_BASE_URL}/groups/${groupId}/members`, { email });
    const group = mockGroups.find(g => g.id === groupId);
    if (group) {
      group.memberCount += 1;
    }
    return {
      success: true,
      message: `Utilizador ${email} adicionado com sucesso!`
    };
  }
};
