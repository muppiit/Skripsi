/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useState, useEffect } from "react";
import { Form, Input, Modal, DatePicker, Select } from "antd";
import { getKelas } from "@/api/kelas";

const { TextArea } = Input;
const { Option } = Select;

const AddStudentForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  religion,
}) => {
  const [form] = Form.useForm();
  const [kelasList, setKelasList] = useState([]);
  const [filteredKelasList, setFilteredKelasList] = useState([]);

  const fetchKelasList = async () => {
    try {
      const result = await getKelas();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setKelasList(content || []);
      }
    } catch (error) {
      console.error("Error fetching kelas data: ", error);
    }
  };

  const handleStudyProgramChange = (studyProgramId) => {
    const nextKelas = (kelasList || []).filter(
      (kelas) => kelas?.studyProgram?.id === studyProgramId
    );
    setFilteredKelasList(nextKelas);
    form.setFieldsValue({ idKelas: undefined, angkatan: undefined });
  };

  const handleKelasChange = (kelasId) => {
    const selectedKelas = (filteredKelasList || []).find(
      (kelas) => kelas.idKelas === kelasId
    );
    form.setFieldsValue({ angkatan: selectedKelas?.angkatan || undefined });
  };

  const handleSubmit = () => {
    form
      .validateFields()
      .then((values) => {
        onOk(values);
      })
      .catch((info) => {
        console.log("Validate Failed:", info);
      });
  };

  useEffect(() => {
    fetchKelasList();
  }, []);

  const formItemLayout = {
    labelCol: {
      xs: { span: 24 },
      sm: { span: 8 },
    },
    wrapperCol: {
      xs: { span: 24 },
      sm: { span: 16 },
    },
  };

  return (
    <Modal
      title="Tambah Siswa"
      visible={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
    >
      <Form form={form} {...formItemLayout}>
        <Form.Item
          label="ID Siswa:"
          name="id"
          rules={[{ required: true, message: "Silahkan isikan id siswa" }]}
        >
          <Input placeholder="ID Siswa" />
        </Form.Item>

        <Form.Item
          label="NISN:"
          name="nisn"
          rules={[{ required: true, message: "NISN wajib diisi" }]}
        >
          <Input placeholder="NISN" />
        </Form.Item>

        <Form.Item
          label="Nama Lengkap:"
          name="name"
          rules={[
            { required: true, message: "Nama lengkap mahasiswa wajib diisi" },
          ]}
        >
          <Input placeholder="Nama Lengkap Mahasiswa" />
        </Form.Item>

        <Form.Item
          label="Tempat Lahir:"
          name="place_born"
          rules={[{ required: true, message: "Tempat Lahir wajib diisi" }]}
        >
          <Input placeholder="Tempat Lahir" />
        </Form.Item>

        <Form.Item
          label="Tanggal Lahir:"
          name="birth_date"
          rules={[{ required: true, message: "Tanggal Lahir wajib diisi" }]}
        >
          <DatePicker placeholder="Tanggal Lahir" />
        </Form.Item>

        <Form.Item
          label="Gender:"
          name="gender"
          rules={[{ required: true, message: "Gender wajib diisi" }]}
        >
          <Select style={{ width: 120 }} placeholder="Gender">
            <Select.Option value="L">Laki-laki</Select.Option>
            <Select.Option value="P">Perempuan</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="Nomor Telepon:"
          name="phone"
          rules={[{ required: true, message: "Nomor telefon wajib diisi" }]}
        >
          <Input type="number" placeholder="Nomor Telefon (62)" />
        </Form.Item>

        <Form.Item
          label="Alamat:"
          name="address"
          rules={[{ required: true, message: "Alamat wajib diisi" }]}
        >
          <TextArea placeholder="Alamat" />
        </Form.Item>

        <Form.Item
          label="Agama:"
          name="religion_id"
          rules={[{ required: true, message: "Silahkan pilih agama" }]}
        >
          <Select style={{ width: 300 }} placeholder="Pilih Agama">
            {religion.map((arr, key) => (
              <Select.Option value={arr.id} key={`religion-${key}`}>
                {arr.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Program Studi:"
          name="idStudyProgram"
          rules={[{ required: true, message: "Silahkan pilih program studi" }]}
        >
          <Select
            placeholder="Pilih Program Studi"
            onChange={handleStudyProgramChange}
          >
            {(studyProgram || []).map((program) => (
              <Option key={program.id} value={program.id}>
                {program.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Kelas:"
          name="idKelas"
          rules={[{ required: true, message: "Silahkan pilih kelas" }]}
        >
          <Select placeholder="Pilih Kelas" onChange={handleKelasChange}>
            {(filteredKelasList || []).map((kelas) => (
              <Option key={kelas.idKelas} value={kelas.idKelas}>
                {kelas.namaKelas}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Angkatan:"
          name="angkatan"
          rules={[{ required: true, message: "Silahkan pilih kelas terlebih dahulu" }]}
        >
          <Input placeholder="Angkatan" disabled />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddStudentForm;
