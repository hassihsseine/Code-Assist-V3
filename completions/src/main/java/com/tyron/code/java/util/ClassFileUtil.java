package com.tyron.code.java.util;

import com.tyron.code.logging.Logging;
import org.slf4j.Logger;
import shadow.com.sun.tools.javac.code.Symbol;
import shadow.com.sun.tools.javac.util.Convert;
import shadow.com.sun.tools.javac.util.Name;
import shadow.javax.lang.model.element.*;
import shadow.javax.lang.model.type.*;

import java.io.UTFDataFormatException;
import java.util.Arrays;

public class ClassFileUtil {

    private static final Logger logger = Logging.get(ClassFileUtil.class);

    public static String[] createFieldDescriptor(final VariableElement ve) {
        assert ve != null;
        String[] result = new String[3];
        Element enclosingElement = ve.getEnclosingElement();
        if (enclosingElement != null && enclosingElement.asType().getKind() == TypeKind.NONE) {
            result[0] = "";  //NOI18N
        } else {
            assert enclosingElement instanceof TypeElement;
            result[0] = encodeClassNameOrArray((TypeElement) enclosingElement);
        }
        result[1] = ve.getSimpleName().toString();
        StringBuilder sb = new StringBuilder();
        encodeType(ve.asType(), sb);
        result[2] = sb.toString();
        return result;
    }

    public static String[] createExecutableDescriptor(final ExecutableElement ee) {
        logger.trace("Calling createExecutableDescriptor: ExecutableElement = {}", ee);
        assert ee != null && ee.asType() != null : "Wrong executable element: " + ee; //NOI18N
        final ElementKind kind = ee.getKind();
        final String[] result = (kind == ElementKind.STATIC_INIT || kind == ElementKind.INSTANCE_INIT) ? new String[2] : new String[3];
        final Element enclosingType = ee.getEnclosingElement();
        if (enclosingType != null && enclosingType.asType().getKind() == TypeKind.NONE) {
            result[0] = ""; //NOI18N
        } else {
            assert enclosingType instanceof TypeElement : enclosingType == null ? "null" : enclosingType.toString() + "(" + enclosingType.getKind() + ")"; //NOI18N
            result[0] = encodeClassNameOrArray((TypeElement) enclosingType);
        }
        logger.trace("Result of encodeClassNameOrArray = {}", result[0]);
        if (kind == ElementKind.METHOD || kind == ElementKind.CONSTRUCTOR) {
            final StringBuilder retType = new StringBuilder();
            if (kind == ElementKind.METHOD) {
                result[1] = ee.getSimpleName().toString();
                if (ee.asType().getKind() == TypeKind.EXECUTABLE) {
                    encodeType(ee.getReturnType(), retType);
                }
            } else {
                result[1] = "<init>";   // NOI18N
                retType.append('V');    // NOI18N
            }
            StringBuilder sb = new StringBuilder();
            sb.append('(');             // NOI18N
            for (VariableElement pd : ee.getParameters()) {
                encodeType(pd.asType(), sb);
            }
            sb.append(')');             // NOI18N
            sb.append(retType);
            result[2] = sb.toString();
        } else if (kind == ElementKind.INSTANCE_INIT) {
            result[1] = "<init>";       // NOI18N
        } else if (kind == ElementKind.STATIC_INIT) {
            result[1] = "<cinit>";      // NOI18N
        } else {
            throw new IllegalArgumentException();
        }

        logger.trace("Result of createExecutableDescriptor = {}", Arrays.toString(result));
        return result;
    }

    public static String encodeClassNameOrArray(TypeElement td) {
        assert td != null;
        CharSequence qname = td.getQualifiedName();
        TypeMirror enclosingType = td.getEnclosingElement().asType();
        if (qname != null && enclosingType != null && enclosingType.getKind() == TypeKind.NONE && "Array".equals(qname.toString())) {     //NOI18N
            return "[";  //NOI18N
        } else {
            return encodeClassName(td);
        }
    }

    public static String encodeClassName(TypeElement td) {
        assert td != null;
        StringBuilder sb = new StringBuilder();
        encodeClassName(td, sb, '.');    // NOI18N
        return sb.toString();
    }

    private static void encodeType(final TypeMirror type, final StringBuilder sb) {
        switch (type.getKind()) {
            case VOID:
                sb.append('V');        // NOI18N
                break;
            case BOOLEAN:
                sb.append('Z');        // NOI18N
                break;
            case BYTE:
                sb.append('B');        // NOI18N
                break;
            case SHORT:
                sb.append('S');        // NOI18N
                break;
            case INT:
                sb.append('I');        // NOI18N
                break;
            case LONG:
                sb.append('J');        // NOI18N
                break;
            case CHAR:
                sb.append('C');        // NOI18N
                break;
            case FLOAT:
                sb.append('F');        // NOI18N
                break;
            case DOUBLE:
                sb.append('D');        // NOI18N
                break;
            case ARRAY:
                sb.append('[');        // NOI18N
                assert type instanceof ArrayType;
                encodeType(((ArrayType) type).getComponentType(), sb);
                break;
            case DECLARED: {
                sb.append('L');        // NOI18N
                TypeElement te = (TypeElement) ((DeclaredType) type).asElement();
                encodeClassName(te, sb, '/');
                sb.append(';');        // NOI18N
                break;
            }
            case TYPEVAR: {
                assert type instanceof TypeVariable;
                TypeVariable tr = (TypeVariable) type;
                TypeMirror upperBound = tr.getUpperBound();
                if (upperBound.getKind() == TypeKind.NULL) {
                    sb.append("Ljava/lang/Object;");       // NOI18N
                } else {
                    encodeType(upperBound, sb);
                }
                break;
            }
            case ERROR: {
                TypeElement te = (TypeElement) ((DeclaredType) type).asElement();
                if (te != null) {
                    sb.append('L');
                    encodeClassName(te, sb, '/');
                    sb.append(';');        // NOI18N
                    break;
                }
            }
            case INTERSECTION: {
                encodeType(((IntersectionType) type).getBounds().get(0), sb);
                break;
            }
            default:
                throw new IllegalArgumentException(
                        String.format(
                                "Unsupported type: %s, kind: %s",   //NOI18N
                                type,
                                type.getKind()));
        }
    }

    public static void encodeClassName(TypeElement te, final StringBuilder sb, final char separator) {
        Name name = ((Symbol.ClassSymbol) te).flatname;
        assert name != null;
        final int nameLength = name.getByteLength();
        final char[] nameChars = new char[nameLength];
        int charLength = Convert.utf2chars(name.getByteArray(), name.getByteOffset(), nameChars, 0, nameLength);
        if (separator != '.') {         //NOI18N
            for (int i = 0; i < charLength; i++) {
                if (nameChars[i] == '.') {  //NOI18N
                    nameChars[i] = separator;
                }
            }
        }
        sb.append(nameChars, 0, charLength);
    }
}
