import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav style={styles.nav}>
      <div style={styles.brand}>
        <Link to="/packages" style={styles.brandLink}>
          TravelAgency
        </Link>
      </div>

      <div style={styles.links}>
        <Link to="/packages" style={styles.link}>
          Paquetes
        </Link>

        {user && (
          <Link to="/my-reservations" style={styles.link}>
            Mis reservas
          </Link>
        )}

        {/* user.roles es array — se verifica con .includes() */}
        {user?.roles?.includes('ADMIN') && (
          <div className="dropdown" style={styles.dropdown}>
            <span style={styles.link}>Administración ▾</span>
            <div style={styles.dropdownMenu}>
              <Link to="/admin/packages" style={styles.dropdownItem}>
                Gestionar paquetes
              </Link>
              <Link to="/admin/users" style={styles.dropdownItem}>
                Gestionar usuarios
              </Link>
              <Link to="/admin/reports" style={styles.dropdownItem}>
                Reportes
              </Link>
            </div>
          </div>
        )}
      </div>

      <div style={styles.auth}>
        {user ? (
          <div style={styles.userSection}>
            <span style={styles.userName}>
              Hola, {user.fullName?.split(' ')[0]}
            </span>
            <button onClick={handleLogout} style={styles.logoutBtn}>
              Cerrar sesión
            </button>
          </div>
        ) : (
          <div style={styles.authLinks}>
            <Link to="/register" style={styles.registerBtn}>
              Registrarse
            </Link>
            <Link to="/login" style={styles.loginBtn}>
              Iniciar sesión
            </Link>
          </div>
        )}
      </div>
    </nav>
  )
}

const styles = {
  nav: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '0 32px',
    height: '64px',
    backgroundColor: '#1a1a2e',
    color: '#fff',
    position: 'sticky',
    top: 0,
    zIndex: 1000,
    boxShadow: '0 2px 8px rgba(0,0,0,0.3)'
  },
  brand: { fontSize: '20px', fontWeight: '700' },
  brandLink: { color: '#e94560', textDecoration: 'none', letterSpacing: '1px' },
  links: { display: 'flex', alignItems: 'center', gap: '24px' },
  link: { color: '#ccc', textDecoration: 'none', fontSize: '14px', cursor: 'pointer', transition: 'color 0.2s' },
  dropdown: { position: 'relative', cursor: 'pointer' },
  dropdownMenu: {
    display: 'none',
    position: 'absolute',
    top: '100%',
    left: 0,
    backgroundColor: '#16213e',
    borderRadius: '8px',
    padding: '8px 0',
    minWidth: '180px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.3)'
  },
  dropdownItem: { display: 'block', padding: '10px 16px', color: '#ccc', textDecoration: 'none', fontSize: '14px' },
  auth: { display: 'flex', alignItems: 'center' },
  userSection: { display: 'flex', alignItems: 'center', gap: '16px' },
  userName: { fontSize: '14px', color: '#ccc' },
  logoutBtn: {
    padding: '8px 16px',
    backgroundColor: 'transparent',
    border: '1px solid #e94560',
    color: '#e94560',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px'
  },
  authLinks: { display: 'flex', gap: '12px', alignItems: 'center' },
  registerBtn: {
    padding: '8px 20px',
    backgroundColor: 'transparent',
    color: '#ccc',
    border: '1px solid #555',
    borderRadius: '6px',
    textDecoration: 'none',
    fontSize: '14px'
  },
  loginBtn: {
    padding: '8px 20px',
    backgroundColor: '#e94560',
    color: '#fff',
    borderRadius: '6px',
    textDecoration: 'none',
    fontSize: '14px',
    fontWeight: '500'
  }
}
