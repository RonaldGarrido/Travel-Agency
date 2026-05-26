import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { createUser } from '../api/userApi'

export default function RegisterPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    phone: '',
    documentId: '',
    nationality: '',
    role: 'CLIENT'
  })
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await createUser(form)
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrar el usuario')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Crear cuenta</h1>
          <p style={styles.subtitle}>Regístrate para empezar a reservar</p>
        </div>

        {error && <div style={styles.error}>{error}</div>}

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.field}>
            <label style={styles.label}>Nombre completo</label>
            <input
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
              placeholder="Juan Pérez"
              required
              style={styles.input}
            />
          </div>
          <div style={styles.field}>
            <label style={styles.label}>Correo electrónico</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="correo@ejemplo.com"
              required
              style={styles.input}
            />
          </div>
          <div style={styles.field}>
            <label style={styles.label}>Contraseña</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Mínimo 8 caracteres"
              required
              minLength={8}
              style={styles.input}
            />
          </div>
          <div style={styles.row}>
            <div style={styles.field}>
              <label style={styles.label}>Teléfono</label>
              <input
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="912345678"
                style={styles.input}
              />
            </div>
            <div style={styles.field}>
              <label style={styles.label}>Documento de identidad</label>
              <input
                name="documentId"
                value={form.documentId}
                onChange={handleChange}
                placeholder="12345678-9"
                style={styles.input}
              />
            </div>
          </div>
          <div style={styles.field}>
            <label style={styles.label}>Nacionalidad</label>
            <input
              name="nationality"
              value={form.nationality}
              onChange={handleChange}
              placeholder="Chilena"
              style={styles.input}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ ...styles.submitBtn, opacity: loading ? 0.7 : 1 }}
          >
            {loading ? 'Registrando...' : 'Crear cuenta'}
          </button>
        </form>

        <p style={styles.loginText}>
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" style={styles.loginLink}>
            Inicia sesión
          </Link>
        </p>
      </div>
    </div>
  )
}

const styles = {
  page: {
    minHeight: 'calc(100vh - 64px)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f5f5f5',
    padding: '32px 16px'
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '16px',
    padding: '40px',
    width: '100%',
    maxWidth: '480px',
    boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
  },
  header: { textAlign: 'center', marginBottom: '32px' },
  title: { fontSize: '28px', fontWeight: '700', color: '#1a1a2e', marginBottom: '8px' },
  subtitle: { color: '#999', fontSize: '14px' },
  error: {
    backgroundColor: '#fff0f0',
    color: '#e94560',
    padding: '12px 16px',
    borderRadius: '8px',
    fontSize: '14px',
    marginBottom: '20px',
    textAlign: 'center'
  },
  form: { display: 'flex', flexDirection: 'column', gap: '16px' },
  field: { display: 'flex', flexDirection: 'column', gap: '6px', flex: 1 },
  label: { fontSize: '14px', fontWeight: '500', color: '#333' },
  input: {
    padding: '12px 16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '14px',
    outline: 'none',
    width: '100%',
    boxSizing: 'border-box'
  },
  row: { display: 'flex', gap: '16px' },
  submitBtn: {
    padding: '14px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    fontSize: '16px',
    fontWeight: '600',
    cursor: 'pointer',
    marginTop: '8px'
  },
  loginText: { textAlign: 'center', marginTop: '24px', fontSize: '14px', color: '#666' },
  loginLink: { color: '#e94560', fontWeight: '500' }
}
