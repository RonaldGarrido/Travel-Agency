import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAllUsers, changeUserStatus } from '../../api/userApi'
import { useAuth } from '../../context/AuthContext'

export default function AdminUsersPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    // user.roles es array — se usa .includes() para verificar el rol ADMIN
    if (!user || !user.roles?.includes('ADMIN')) {
      navigate('/')
      return
    }
    fetchUsers()
  }, [user, navigate])

  const fetchUsers = async () => {
    try {
      const response = await getAllUsers()
      setUsers(response.data)
    } catch {
      setError('Error al cargar los usuarios')
    } finally {
      setLoading(false)
    }
  }

  const handleStatusChange = async (id, status) => {
    try {
      await changeUserStatus(id, status)
      fetchUsers()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al cambiar el estado')
    }
  }

  const statusColors = {
    ACTIVE: { color: '#2d8a2d', bg: '#e8f8e8' },
    INACTIVE: { color: '#999', bg: '#f5f5f5' },
    BLOCKED: { color: '#e94560', bg: '#fff0f0' }
  }

  const roleColors = {
    ADMIN: { color: '#534AB7', bg: '#EEEDFE' },
    CLIENT: { color: '#1a6ea8', bg: '#e8f4fd' }
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>Gestión de usuarios</h1>

        {error && <div style={styles.error}>{error}</div>}

        {loading ? (
          <p style={styles.center}>Cargando...</p>
        ) : (
          <div style={styles.tableWrap}>
            <table style={styles.table}>
              <thead>
                <tr style={styles.thead}>
                  <th style={styles.th}>Nombre</th>
                  <th style={styles.th}>Email</th>
                  <th style={styles.th}>Teléfono</th>
                  <th style={styles.th}>Rol</th>
                  <th style={styles.th}>Estado</th>
                  <th style={styles.th}>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => {
                  const sc = statusColors[u.status] || statusColors.INACTIVE
                  const rc = roleColors[u.role] || roleColors.CLIENT
                  return (
                    <tr key={u.id} style={styles.tr}>
                      <td style={styles.td}>{u.fullName}</td>
                      <td style={styles.td}>{u.email}</td>
                      <td style={styles.td}>{u.phone || '-'}</td>
                      <td style={styles.td}>
                        <span style={{ ...styles.badge, color: rc.color, backgroundColor: rc.bg }}>
                          {u.role}
                        </span>
                      </td>
                      <td style={styles.td}>
                        <span style={{ ...styles.badge, color: sc.color, backgroundColor: sc.bg }}>
                          {u.status}
                        </span>
                      </td>
                      <td style={styles.td}>
                        <select
                          value={u.status}
                          onChange={(e) => handleStatusChange(u.id, e.target.value)}
                          style={styles.statusSelect}
                        >
                          <option value="ACTIVE">Activo</option>
                          <option value="INACTIVE">Inactivo</option>
                          <option value="BLOCKED">Bloqueado</option>
                        </select>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

const styles = {
  page: { minHeight: 'calc(100vh - 64px)', backgroundColor: '#f5f5f5', padding: '32px 16px' },
  container: { maxWidth: '1000px', margin: '0 auto' },
  title: { fontSize: '24px', fontWeight: '700', color: '#1a1a2e', marginBottom: '24px' },
  error: {
    backgroundColor: '#fff0f0', color: '#e94560', padding: '12px',
    borderRadius: '8px', marginBottom: '16px', fontSize: '14px'
  },
  tableWrap: { overflowX: 'auto' },
  table: {
    width: '100%', borderCollapse: 'collapse', backgroundColor: '#fff',
    borderRadius: '12px', overflow: 'hidden', boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  thead: { backgroundColor: '#1a1a2e' },
  th: { padding: '14px 16px', textAlign: 'left', fontSize: '13px', fontWeight: '500', color: '#fff' },
  tr: { borderBottom: '1px solid #f0f0f0' },
  td: { padding: '14px 16px', fontSize: '14px', color: '#333' },
  badge: { padding: '4px 10px', borderRadius: '20px', fontSize: '12px', fontWeight: '500' },
  statusSelect: { padding: '6px 8px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '12px', cursor: 'pointer' },
  center: { textAlign: 'center', color: '#999', padding: '40px' }
}
