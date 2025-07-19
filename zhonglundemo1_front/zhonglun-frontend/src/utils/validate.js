export const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

export const validatePassword = (password) => {
  return password.length >= 6
}

export const validatePhone = (phone) => {
  const re = /^1[3-9]\d{9}$/
  return re.test(phone)
}

export const validatePrice = (price) => {
  return !isNaN(price) && price > 0
}

export const validateStock = (stock) => {
  return !isNaN(stock) && stock >= 0
}